function MemeController($scope, $resource, $routeParams) {
	$scope.isCreating = true;

	//api.fetch(function(data) {
	//	$scope.memes = data;
	//});
	var Meme = $resource('/rest/meme:id', {});
	var memes = Meme.query(function (data) {
	});
	$scope.memes = memes;

	$scope.upload = function() {
		console.log(arguments);
	};

	$scope.fileChanged = function() {
		console.log(arguments);
	}
}
