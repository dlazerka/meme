/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme.compose', ['ngResource', 'me.lazerka.ng.upload'])
	.controller('ComposeController', function($scope, $resource, uploadService) {
		$scope.newMeme = {};

		$scope.$on('fileUploaded', function(event, blobInfo) {
			$scope.newMeme.blobKey = blobInfo.blobKey;
			$scope.newMeme.filename = blobInfo.filename;
			$scope.newMeme.url = '/rest/image/' + blobInfo.blobKey;
		});
	});
