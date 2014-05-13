/** Author Dzmitry Lazerka */

(function() {
	var app = angular.module('app', ['ngRoute', 'ngResource']);

	app.config(function ($routeProvider, $locationProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'meme/meme.html',
				controller: MemeController
			})
			.otherwise({
				redirectTo: '/'
			});

		$locationProvider.html5Mode(true);
	});

	app.directive('dropHere', function() {
		return {
			'scope': false,
			//'require': '^someDirective',
			'link': function($scope, element, attrs) {
				//element.addEventListener('dragover', this.preventEvent, false);
				//element.addEventListener('dragenter', this.preventEvent, false);

				element.bind('dragover dragleave', function (event) {
					// !!! Caution, dragover is expensive (like mousemove) !!!

					console.log(event.type);
					if (!isFileDrag(event)) {
						return ;
					}
					element.toggleClass('fileOverMe', event.type == 'dragover');

					event.preventDefault();
					event.stopPropagation();
				});

				element.bind('drop', function (event) {
					console.log(event.type);
					event.preventDefault();
					event.stopPropagation();
				});

				function isFileDrag(dragEvent) {
					var dataTransfer = dragEvent.dataTransfer || dragEvent.originalEvent.dataTransfer;
					if (!dataTransfer) {
						return false;
					}

					for(var i = 0; i < dataTransfer.types.length; i++) {
						if (dataTransfer.types[i] === 'Files') {
							return true;
						}
					}
					return false;
				}
			}
		};
	});
})();

