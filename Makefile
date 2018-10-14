dev/cycle:
	lein descjop-figwheel

dev/app/macos:
	lein cljsbuild once dev-main dev-front
	./electron/Electron.app/Contents/MacOS/Electron app/dev

dev/app/linux:
	lein cljsbuild once dev-main dev-front
	./electron/electron app/dev

dev/grunt:
	grunt dev
