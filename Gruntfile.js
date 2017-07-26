module.exports = function(grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        "download-electron": {
            version: "1.7.5",
            outputDir: "./electron",
            // if you want to download electron into project directory
            // downloadDir: ".electron-download",
            rebuild: true
        },
        sass: {
            dev: {
                options: {
                    sourcemap: 'auto'
                },
                src: [// "./node_modules/semantic-ui-sass/semantic-ui.scss",
                      "./src_front/assets/scss/app.scss"],
                dest: "./app/dev/css/app.css"
            }
        },
        watch: {
            css: {
                files: 'src_front/assets/scss/*.scss',
                tasks: ['sass']
            }
        }
    });

    grunt.loadNpmTasks('grunt-download-electron');
    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.registerTask('dev', ['watch']);

};
