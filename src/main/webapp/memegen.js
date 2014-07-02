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
		var memeResource = $resource('/rest/meme/:ownerEmail/:id', {});

		$scope.memes = memeResource.query(function(data) {});

		$scope.$on('compose.created', function(event, memePromise) {
			$scope.memes.unshift(memePromise);
		});

		$scope.remove = function(meme) {

			var resource = memeResource.remove({
				'ownerEmail': meme.ownerEmail,
				id: meme.id
			});
			resource.$promise.then(function() {
				console.log('Meme ' + meme.id + ' deleted on server.');
			});
			$scope.memes = _.reject($scope.memes, function(el) {
				return el.id == meme.id;
			});
			//_.findWhere($scope.memes, {id: meme.id});
			//var i = $scope.memes.findIndex(function(el) {
			//	return el.id === meme.id;
			//});
		}
	});

