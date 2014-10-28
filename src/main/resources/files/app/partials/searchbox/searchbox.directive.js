'use strict';

angular.module('onsComponents')
	.directive('searchBox', ['$location', '$route',
		function($location, $route) {
			return {
				restrict: 'E',
				templateUrl: 'app/partials/searchbox/searchbox.html',
				controller: function($scope) {
					$scope.goToSearch = function(searchTerm) {
						if (!searchTerm) {
							return
						}
						$location.path('/search')
						$location.search('q', searchTerm)

						//Clear page parameter if any
						$location.search('page', null)
						//Re-initializes controllers. Fixes searching on search results page when searching the same term
						$route.reload()
					}
				}
			}
		}
	])