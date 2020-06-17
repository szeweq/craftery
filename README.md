# MCTool
A creative tool for exploring and creating Minecraft resources, managing servers and helping mod development.
This is meant to replace [MCPM](https://github.com/Szewek/mcpm), a mod "package manager" which is no longer being developed.

This application consists into two parts: Core (Go) and GUI (Vue).

Most features haven't been implemented yet. **More new ideas are welcome.**

## Requirements
- Go (1.13 and above)
- Node.js
- Yarn

## Installation
1. Clone this repository
2. Build GUI by typing these commands:
   ```
   cd web
   yarn build
   ```
3. Build Go program using `go build`

## Running
Just type `mctool` in your Terminal/CMD. It should open a web page in a default browser.

## Development
If you want to make changes in UI files, use `yarn serve` command.

Changes in Go files require compiling and restarting an app.