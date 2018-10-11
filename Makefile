dev/cycle:
	lein descjop-figwheel
dev/app:
	lein cljsbuild once dev-main dev-front
	./electron/Electron.app/Contents/MacOS/Electron app/dev
dev/grunt:
	grunt dev
