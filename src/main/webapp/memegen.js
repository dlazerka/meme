/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen', [
	'me.lazerka.memegen.meme',
	'me.lazerka.memegen.meme.compose',
	'ngRoute'
])
	.config(function($routeProvider, $locationProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'memes.html',
				controller: 'MemesController'
			})
			.otherwise({
				redirectTo: '/'
			});

		$locationProvider.html5Mode(true);
	})
	.controller('MemesController', function($rootScope, $scope, $resource) {
		var Meme = $resource('/rest/meme:id', {});
		$scope.memes = Meme.query(function(data) {
			// hm, nothing
		});
	});

