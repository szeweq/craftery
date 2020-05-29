package jclass

import (
	"errors"
	"fmt"
	"io"
)

const printErrBytes = false

type (
	// JavaClassScanner reads Java class file to extract its data (fields, methods, etc.). This is very inefficient.
	JavaClassScanner struct {
		r      io.ReadSeeker
		idx    map[uint16]Ref
		Fields []FieldInfo
	}
	// Ref is a reference of class data (constant string, class ID, methods, etc.)
	Ref interface {
		Deref(*JavaClassScanner, int) string
	}
	stringConst string
	classRef    uint16
	stringRef   uint16
	fieldRef    struct {
		rc, rn uint16
	}
	methodRef struct {
		rc, rn uint16
	}
	methodTypeRef uint16
	FieldInfo     struct {
		Access uint16
		Name   string
		Desc   string
		Attrs  map[string]string
	}
	bufread [4]byte
)

var errNotJavaClass = errors.New("Not a Java class")

func NewJavaClassScanner(rs io.ReadSeeker) *JavaClassScanner {
	return &JavaClassScanner{r: rs, idx: make(map[uint16]Ref)}
}
func (jc *JavaClassScanner) Reset(rs io.ReadSeeker) {
	*jc = JavaClassScanner{r: rs, idx: make(map[uint16]Ref)}
}
func (jc *JavaClassScanner) Scan() error {
	var br bufread
	rs := jc.r
	if magic, _ := br.readU32(rs); magic != 0xCAFEBABE {
		return errNotJavaClass
	}
	rs.Seek(8, io.SeekStart)
	cpc, _ := br.readU16(rs)
	var tbx [1]byte
	for i := uint16(1); i < cpc; i++ {
		rs.Read(tbx[:])
		tb := tbx[0]
		switch tb {
		case 1:
			sl, _ := br.readU16(rs)
			bb := make([]byte, sl)
			io.ReadFull(rs, bb)
			jc.idx[i] = stringConst(bb)
		case 7:
			cl, _ := br.readU16(rs)
			jc.idx[i] = classRef(cl)
		case 8:
			cl, _ := br.readU16(rs)
			jc.idx[i] = stringRef(cl)
		case 9:
			rc, _ := br.readU16(rs)
			rn, _ := br.readU16(rs)
			jc.idx[i] = fieldRef{rc: rc, rn: rn}
		case 10:
			rc, _ := br.readU16(rs)
			rn, _ := br.readU16(rs)
			jc.idx[i] = methodRef{rc: rc, rn: rn}
		case 15:
			rs.Seek(3, io.SeekCurrent)
		case 16:
			cl, _ := br.readU16(rs)
			jc.idx[i] = methodTypeRef(cl)
		default:
			if tb == 3 || tb == 4 || (tb >= 10 && tb <= 12) || tb == 17 || tb == 18 {
				rs.Seek(4, io.SeekCurrent)
			} else if tb == 16 || tb == 19 || tb == 20 {
				rs.Seek(2, io.SeekCurrent)
			} else if tb == 5 || tb == 6 {
				rs.Seek(8, io.SeekCurrent)
			}
		}
	}
	rs.Seek(6, io.SeekCurrent)
	//var tci, sci uint16
	//binary.Read(rs, be, &tci)
	//binary.Read(rs, be, &sci)
	//fmt.Printf("[%s] SUPER %s\n", name, jc.TryDeref(sci, 1))

	// Interfaces
	vs, _ := br.readU16(rs)
	ni := 0
	for i := uint16(0); i < vs; i++ {
		if im, _ := br.readU16(rs); im == 0 {
			ni++
		}
	}
	if ni > 1 {
		return makeErr(rs, "Class cannot implement %d null interface(s) (all %d)", ni, vs)
	}

	// Fields
	vs, _ = br.readU16(rs)
	for i := uint16(0); i < vs; i++ {
		var vh0, vh1, vh2, vh3 uint16
		vh0, _ = br.readU16(rs)
		vh1, _ = br.readU16(rs)
		vh2, _ = br.readU16(rs)
		vh3, _ = br.readU16(rs)
		as := make(map[string]string, vh3)
		for j := uint16(0); j < vh3; j++ {
			s1, s2 := jc.readAttrInfo()
			as[s1] = s2
		}
		jc.Fields = append(jc.Fields, FieldInfo{Access: vh0, Name: jc.TryDeref(vh1, 1), Desc: jc.TryDeref(vh2, 1), Attrs: as})
	}
	return nil
}
func (jc *JavaClassScanner) readAttrInfo() (s1 string, s2 string) {
	var br bufread
	rs := jc.r
	ani, _ := br.readU16(rs)
	s1 = jc.DerefString(ani, 1)
	al, _ := br.readU32(rs)
	switch s1 {
	case "Signature":
		si, _ := br.readU16(rs)
		s2 = jc.DerefString(si, 1)
	default:
		if al > 8 {
			s2 = "<TOO LARGE>"
			rs.Seek(int64(al), io.SeekCurrent)
		} else {
			bb := make([]byte, al)
			rs.Read(bb)
			s2 = fmt.Sprintf("%q", bb)
		}
	}
	return
}
func (b bufread) readU16(r io.Reader) (uint16, error) {
	if _, e := r.Read(b[:2]); e != nil {
		return 0, e
	}
	return uint16(b[1]) | uint16(b[0])<<8, nil
}
func (b bufread) readU32(r io.Reader) (uint32, error) {
	if _, e := r.Read(b[:]); e != nil {
		return 0, e
	}
	return uint32(b[3]) | uint32(b[2])<<8 | uint32(b[1])<<16 | uint32(b[0])<<24, nil
}
func makeErr(r io.Reader, f string, a ...interface{}) error {
	if printErrBytes {
		rest := make([]byte, 64)
		r.Read(rest)
		return fmt.Errorf("%s\nHere are 64 following bytes: %q", fmt.Sprintf(f, a...), rest)
	}
	return fmt.Errorf(f, a...)
}
func (jc *JavaClassScanner) TryDeref(i uint16, lvl int) string {
	if lvl > 7 {
		return "<TOO DEEP>"
	}
	if r, ok := jc.idx[i]; ok {
		return r.Deref(jc, lvl+1)
	}
	return "<NIL>"
}
func (jc *JavaClassScanner) DerefString(i uint16, lvl int) string {
	if lvl > 7 {
		return "<TOO DEEP>"
	}
	r, ok := jc.idx[i]
	if !ok {
		return "<NIL>"
	}
	switch x := r.(type) {
	case stringConst:
		return string(x)
	case stringRef:
		return jc.DerefString(uint16(x), lvl+1)
	}
	return "<INVALID>"
}

func (s stringConst) Deref(jc *JavaClassScanner, lvl int) string {
	return string(s)
}
func (cr classRef) Deref(jc *JavaClassScanner, lvl int) string {
	return "(" + jc.TryDeref(uint16(cr), lvl) + ")"
}
func (sr stringRef) Deref(jc *JavaClassScanner, lvl int) string {
	return jc.TryDeref(uint16(sr), lvl)
}
func (fr fieldRef) Deref(jc *JavaClassScanner, lvl int) string {
	return "{F " + jc.TryDeref(fr.rc, lvl) + ": " + jc.TryDeref(fr.rn, lvl) + "}"
}
func (mr methodRef) Deref(jc *JavaClassScanner, lvl int) string {
	return "{M " + jc.TryDeref(mr.rc, lvl) + ": " + jc.TryDeref(mr.rn, lvl) + "}"
}
func (mtr methodTypeRef) Deref(jc *JavaClassScanner, lvl int) string {
	return "{T " + jc.TryDeref(uint16(mtr), lvl) + "}"
}
