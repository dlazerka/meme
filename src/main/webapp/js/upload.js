/** Author Dzmitry Lazerka */

angular.module('me.lazerka.ng.upload', [])
	/**
	 * Common routine for uploading file.
	 *
	 * Events:
	 *     fileUploadStarted -- after file has read from filesystem, but before upload started or even upload url requested.
	 *     fileUploaded -- after file was uploaded successfully.
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

			var fr = new FileReader();
			fr.readAsDataURL(file);
			fr.onload = function (event) {
				var dataUrl = event.target.result;
				$scope.$apply(function() {
					$scope.file = {
						blobKey: null,
						fileName: file.name,
						size: file.size,
						url: dataUrl
					};
				});
			};

			$http.get('/rest/image/url-for-upload')
				.success(function(url) {
					var formData = new FormData();
					formData.append('file', file);
					$http({
						url: url,
						method: 'POST',
						data: formData,
						// Prevent Angular from serializing data.
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

							$scope.file = {
								blobKey: response.blobKey,
								fileName: response.filename, // note case
								size: response.size,
								url: '/rest/image/' + response.blobKey
							};
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
			scope: {
				file: '=file'
			},
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
			scope: {
				file: '=file'
			},
			link: function($scope, element) {

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

