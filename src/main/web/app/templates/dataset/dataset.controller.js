(function() {

	'use strict';

	angular.module('onsTemplates')
		.controller('DatasetController', ['$scope', 'Downloader', DatasetController])

	function DatasetController($scope, Downloader) {
		var ctrl = this
		ctrl.sidebarUrl = "app/templates/dataset/datasetsidebar.html"
		var data = ctrl.data = $scope.taxonomy.data
		ctrl.timeseries = false
		ctrl.timeseriesItem

		initialize()

		function initialize() {
			var downloads = data.downloads
			for (var i = 0; i < downloads.length; i++) {
				if (downloads[i].cdids) {
					ctrl.timeseries = true
					ctrl.timeseriesItem = downloads[i]
					break
				}
			};
		}

		function downloadXls() {
			download('xlsx');
		}

		function downloadCsv() {
			download('csv');
		}

		function download(type) {
			var downloadRequest = {
				type: type,
				cdidList: ctrl.timeseriesItem.cdids
			};

			var fileName = ctrl.timeseriesItem.title + '.' + downloadRequest.type;
			Downloader.downloadFile(downloadRequest, fileName);
		}

		//Expose functions
		angular.extend(ctrl, {
			downloadXls: downloadXls,
			downloadCsv: downloadCsv
		})

	}

})();