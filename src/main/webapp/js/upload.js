/** Author Dzmitry Lazerka */

angular.module('me.lazerka.ng.upload', [])
/**
 * Common routine for uploading file.
 */
	.service('uploadService', function($http) {
		this.uploadFile = function(file, $scope) {
			if (!file.size) {
				alert("File size is 0, is it a file?");
				return;
			} else if (file.size > (32 << 20)) {
				alert("Sorry, files larger than 32 MB aren't supported.");
				return;
			}

			$http.get('/rest/image/url-for-upload')
				.success(function(url) {
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
						.success(function(response, code, fn, req) {
							if (!response.blobKey) {
								alert('Not a blobInfo response from ' + req.url + ': ' + response);
							}

							$scope.$emit('fileUploaded', response);
							//$scope.onFileUploaded(entity);
						})
						.error(function(entity, code, fn, req) {
							alert(entity);
						});
				});
		};
	})
/**
 * Angular doesn't handle onchange for <input type="file">-s.
 */
	.directive('myOnchange', function(uploadService) {
		return {
			link: function($scope, element, attrs) {
				element.bind('change', function(event) {
					var files = event.target.files;
					if (!files) {
						throw Error('No files in ' + event.target);
					}
					var file = files[0];

					uploadService.uploadFile(file, $scope);
				});
			}
		};
	})
/**
 * Dra&drop handler.
 */
	.directive('dropHere', function(uploadService) {
		return {
			'link': function($scope, element) {

				element.bind('dragover dragleave', function(event) {
					// !!! Caution, dragover is expensive (like mousemove) !!!
					if (!isFileDrag(event)) {
						return;
					}

					event.preventDefault();
					event.stopPropagation();

					element.toggleClass('fileOverMe', event.type == 'dragover');
				});

				element.bind('drop', function(event) {
					event.preventDefault();
					event.stopPropagation();
					element.removeClass('fileOverMe');

					var file = event.dataTransfer.files[0];

					uploadService.uploadFile(file, $scope);
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

