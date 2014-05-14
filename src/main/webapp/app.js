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

	app.directive('dropHere', function($http) {
		return {
			'scope': false,
			'link': function($scope, element, attrs) {

				element.bind('dragover dragleave', function (event) {
					// !!! Caution, dragover is expensive (like mousemove) !!!
					if (!isFileDrag(event)) {
						return ;
					}

					event.preventDefault();
					event.stopPropagation();

					element.toggleClass('fileOverMe', event.type == 'dragover');
				});

				element.bind('drop', function (event) {
					event.preventDefault();
					event.stopPropagation();
					element.removeClass('fileOverMe');

					var file = event.dataTransfer.files[0];

					if (!file.size) {
						alert("File size is 0, is it a file?");
						return;
					} else if (file.size > (32 << 20)) {
						alert("Sorry, files larger than 32 MB aren't supported.");
						return;
					}

					//uploadUrlPromise.then(function(url) {
					uploadFile(file);

					//}, function() {
					//	alert("Unable to retrieve URL for upload.");
					//});
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

				var uploadUrlPromise = getUrlForUploadPromise();
				function getUrlForUploadPromise() {
					return $http.get('/rest/image/url-for-upload');
				}

				function uploadFile(file) {
					uploadUrlPromise.success(function(url) {
						var formData = new FormData();
						formData.append('file', file);
						$http({
							url: url,
							method: 'POST',
							data: formData,
							// Prevent Angular to serialize data.
							transformRequest: angular.identity,
							headers: {
								// Browser will set 'multipart/form-data' and correct boundary.
								'Content-Type': undefined
							}
						})
							.success(function(entity, code, fn, req) {
								// Hooray, entity is URL!
							})
							.error(function(entity, code, fn, req) {
								alert(entity);
							});
					});

					// Refresh upload.
					uploadUrlPromise = getUrlForUploadPromise();

					/*
					 var fr = new FileReader();
					 fr.onload = function() {
					 var content = fr.result;
					 el('text').val(CONTENT);
					 };
					 fr.readAsBinaryString(file);
					 */
				}
			}
		};
	});
})();

