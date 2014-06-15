/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme.compose', ['ngResource', 'me.lazerka.ng.upload'])
	.controller('ComposeController', function($scope, $resource, uploadService) {

		$scope.$on('nav.create', function(event, blobInfo) {
			$scope.meme = {};
		});

		$scope.submit = function() {
			var memeResource = $resource('/rest/meme:id', {});
			var memePromise = memeResource.save($scope.meme, function(meme, headersFn) {
				// Nothing.
				console.log(arguments);
			});

			$scope.$emit('compose.created', memePromise);

			$scope.meme = null;
		};

		$scope.cancel = function() {
			// TODO: inform server to delete blob.
			$scope.meme = null;
		}
	});
