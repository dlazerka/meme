/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme.compose', ['ngResource', 'me.lazerka.ng.upload'])
	.controller('ComposeController', function($scope, $resource, uploadService) {

		$scope.$on('nav.create', function(event, blobInfo) {
			$scope.meme = {};
		});

		$scope.$on('fileUploadStarted', function(event, file) {
			var fr = new FileReader();
			fr.readAsDataURL(file);
			fr.onload = function (event) {
				var url = event.target.result;
				$scope.meme.image = {
					blobKey: null,
					fileName: file.name,
					size: file.size,
					url: url
				};
			};
		});

		$scope.$on('fileUploaded', function(event, blobInfo) {
			$scope.meme.image = {
				blobKey: blobInfo.blobKey,
				fileName: blobInfo.filename, // note case
				size: blobInfo.size,
				url: '/rest/image/' + blobInfo.blobKey
			};
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
