/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen', [
	'me.lazerka.memegen.meme',
	'me.lazerka.memegen.meme.compose',
	'ngRoute'
])
	.constant('MEME_URL', '/rest/meme/:ownerEmail/:id')
	.config(function($routeProvider, $locationProvider, $httpProvider) {
		$routeProvider
			.when('/', {
				controller: 'MemesController'
			})
			.otherwise({
				redirectTo: '/'
			});

		$locationProvider.html5Mode(true);


		// Register $http interceptor to provide common AJAX error behavior.
		$httpProvider.interceptors.push(function($q) {
			return {
				// data, status, headersFn, config
				'responseError': function(rejection) {
					alert(rejection.status + ' ' + rejection.statusText + '\n' + rejection.data);
					return $q.reject(rejection);
				}
			};
		});
	})
	.controller('MemegenController', function($rootScope, $scope, $resource, MEME_URL) {
		// TODO: extract into a Service maybe?
		var memeResource = $resource(MEME_URL, {});

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

