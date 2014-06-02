/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen', [
	'me.lazerka.ng.upload',
	'me.lazerka.memegen.meme',
	'ngRoute'
])
	.config(function($routeProvider, $locationProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'meme/meme.html',
				controller: 'MemeController'
			})
			.otherwise({
				redirectTo: '/'
			});

		$locationProvider.html5Mode(true);
	});
