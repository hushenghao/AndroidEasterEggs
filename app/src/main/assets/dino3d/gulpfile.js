var
	gulp			= require("gulp"),
	livereload		= require("gulp-livereload"),
	sass			= require("gulp-sass"),
	autoprefixer	= require("gulp-autoprefixer"),
	cleancss		= require("gulp-clean-css"),
	rename			= require("gulp-rename"),
	rigger			= require("gulp-rigger"),
	minify			= require("gulp-minify");

gulp.task("reload-css", function() {
	return gulp.src('./css/*.scss')
	.pipe(sass().on('error', sass.logError))
	.pipe(autoprefixer({
		browsers: ['last 3 versions'],
		cascade: false
	}))
	.pipe(gulp.dest('./css/'))
	.pipe(cleancss({compatibility: 'ie8'}))
	.pipe(rename({suffix: '.min'}))
	.pipe(gulp.dest('./css/'))
	.pipe(livereload());
});

gulp.task("reload-js", function() {
	return gulp.src('./js/src/build.js')
	.pipe(rigger())
	.pipe(minify({
		ext: {
			min: '.min.js',
			mangle: false,
			noSource: true,
			preserveComments: 'all'
		}
	}))
	.pipe(gulp.dest('./js'))
});

gulp.task("default", function() {
	livereload.listen();
	gulp.watch('./css/*.scss', gulp.series('reload-css'));
	gulp.watch('./js/src/*.js', gulp.series('reload-js'));
	gulp.watch('./js/src/*/*.js', gulp.series('reload-js'));
});