/** Author Dzmitry Lazerka */

angular.module('me.lazerka.memegen.meme', [
	'ngResource',
	'me.lazerka.ng.upload'
])
	.controller('MemeController', function($scope, $resource) {
		var Meme = $resource('/rest/meme:id', {});
		$scope.memes = Meme.query(function(data) {
		});
	});
