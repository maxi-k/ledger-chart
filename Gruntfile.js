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
            dev_bootstrap: {
                options: { sourcemap: 'auto' },
                files: [{ src: './node_modules/semantic-ui-sass/semantic-ui.scss',
                          dest: './app/dev/css/base.css' }]
            },
            dev: {
                options: { sourcemap: 'auto' },
                files: [{ src: './src_front/assets/scss/app.scss',
                          dest: './app/dev/css/app.css' }]
            }
        },
        copy: {
            dev: {
                files: [{ expand: true,
                          cwd: './node_modules/semantic-ui-sass/icons/',
                          src: '*',
                          dest: './app/icons/' }]
            }
        },
        watch: {
            dev: {
                files: 'src_front/assets/scss/*.scss',
                tasks: ['sass:dev']
            }
        }
    });

    grunt.loadNpmTasks('grunt-download-electron');
    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.registerTask('dev', ['sass:dev_bootstrap', 'sass:dev',
                               'copy:dev', 'watch:dev']);

};
