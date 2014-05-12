/** Author Dzmitry Lazerka */

(function() {
	var app = angular.module('app', ['ngRoute', 'ngResource']);
	app.config(function ($routeProvider, $locationProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'meme/meme.html',
				controller: MemeController
			})
			.otherwise({
				redirectTo: '/'
			});

		$locationProvider.html5Mode(true);
	});
})();

