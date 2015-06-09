(function() {
	'use strict';

	angular.module('onsTemplates')
		.controller('T1Controller', ['$scope', '$window', 'Taxonomy', T1Controller])

	function T1Controller($scope, $window, Taxonomy) {
		var t1 = this
		var sections = $scope.taxonomy.data.sections
		t1.sparklineHeight = 50
		t1.sparklineWidth = 120
		initialize()
		
		function initialize() {
			for (var i = 0; i < sections.length; i++) {
					loadItemData(sections[i].statistics)
			};

		}

        //Listens resize event to switch screen type to mobile or desktop
        function listenResize() {
          angular.element($window).bind('resize', function() {
            resolveChartSize()
            $scope.$apply()
          })
        }

        function resolveChartSize() {
          if ($window.innerWidth < 800) {
            t1.sparklineHeight = 100
            t1.sparklineWidth = 200
          } else {
            t1.sparklineHeight = 50
            t1.sparklineWidth = 120
          }
        }

		function loadItemData(item) {
			Taxonomy.loadItem(item)
				.then(function(data) {
					item.chartData = Taxonomy.resolveChartData(item)
				}, handleDataLoadError)
		}

		function handleDataLoadError(err) {

		}

	}

})();