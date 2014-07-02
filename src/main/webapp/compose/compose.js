/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme.compose', ['ngResource', 'me.lazerka.ng.upload'])
	.controller('ComposeController', function($scope, $resource, MEME_URL) {

		$scope.captions = {
			top: '',
			bottom: ''
		};

		$scope.$on('nav.create', function(event, blobInfo) {
			$scope.meme = {};
		});

		$scope.submit = function() {
			var memeResource = $resource(MEME_URL, {});
			$scope.meme.captions = [];
			if ($scope.captions.top.trim().length) {
				$scope.meme.captions.push({'text': $scope.captions.top, 'topPx': 20});
			}
			if ($scope.captions.bottom.trim().length) {
				$scope.meme.captions.push({'text': $scope.captions.bottom, 'topPx': 250});
			}

			var memePromise = memeResource.save($scope.meme);
			memePromise.$promise.then(
				// callback
				function(meme, headersFn) {
					console.log("Meme saved on server: " + meme.id);
				},
				// errback
				function(response) {
					alert(response.statusText);
				}
			);

			$scope.$emit('compose.created', memePromise);

			$scope.meme = null;
		};

		$scope.cancel = function() {
			// TODO: inform server to delete blob.
			$scope.meme = null;
		}
	});
