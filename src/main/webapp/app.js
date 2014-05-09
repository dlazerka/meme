/** Author Dzmitry Lazerka */
angular.module('app', ['ngResource'])
	.factory('api', function ($resource) {
		return {
			/*
			 fetch: function(callback) {
			 var Meme = $resource('/rest/meme:id', {});

			 var meme = Meme.query(function() {

			 });
			 }
			 */
		};
	})
	.controller('MemeController', function ($scope, $resource, api) {
		//api.fetch(function(data) {
		//	$scope.memes = data;
		//});
		var Meme = $resource('/rest/meme:id', {});
		var memes = Meme.query(function () {

		});
		$scope.memes = memes;

	});
