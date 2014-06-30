/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen', [
	'me.lazerka.memegen.meme',
	'me.lazerka.memegen.meme.compose',
	'ngRoute'
])
	.config(function($routeProvider, $locationProvider) {
		$routeProvider
			.when('/', {
				controller: 'MemesController'
			})
			.otherwise({
				redirectTo: '/'
			});

		$locationProvider.html5Mode(true);
	})
	.controller('MemegenController', function($rootScope, $scope, $resource) {
		// TODO: extract into a Service maybe?
		var memeResource = $resource('/rest/meme:id', {});

		$scope.memes = memeResource.query(function(data) {});

		$scope.$on('compose.created', function(event, memePromise) {
			$scope.memes.unshift(memePromise);
		});
	});

