/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme', ['ngResource'])
	.controller('MemeController', function($scope, $resource) {
		$scope.isCreating = true;

		//api.fetch(function(data) {
		//	$scope.memes = data;
		//});
		var Meme = $resource('/rest/meme:id', {});
		$scope.memes = Meme.query(function(data) {
		});

		$scope.upload = function() {
			console.log(arguments);
		};

		/** Called not by Angular. */
		$scope.onFileChange = function(event) {
			var files = event.target.files;
			if (!files) {
				throw Error('No files in ' + event.target);
			}
			var file = files[0];
		};

		$scope.onupload = function() {
			console.log(arguments);
		};

		$scope.onupload = function() {
			console.log(arguments);
		};

		//$scope.file = {};
		//
		//$scope.file.onChange = function() {
		//	console.log(arguments);
		//}
	});
