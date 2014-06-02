/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme.compose', ['ngResource', 'me.lazerka.ng.upload'])
	.controller('ComposeController', function($scope, $resource, uploadService) {

		$scope.$on('createClicked', function() {
			$scope.show = true;
		});

		$scope.$on('fileUploaded', function() {
			$scope.file = uploadService.file;
		});
	});
