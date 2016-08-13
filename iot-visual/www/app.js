app = angular.module('IoTVisual', ['ngRoute', 'ngAnimate','ngMaterial','ngResource','IoTVisual.services'])
.config(['$routeProvider', '$locationProvider',
	function($routeProvider, $locationProvider) {

		$routeProvider
		.when('/', {
			templateUrl: 'first.html',
			controller: 'FirstCtrl',
			controllerAs: 'first'
		})
        .when('/counters', {
			templateUrl: 'counters.html',
			controller: 'CountersCtrl',
			controllerAs: 'counters'
		})
		.otherwise({
			redirectTo: '/'
		});

		$locationProvider.html5Mode(false);
		$locationProvider.hashPrefix('!');
	}])
.controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $log, $location) {

	$scope.toggleLeft = buildDelayedToggler('left');
	/*
	 * Supplies a function that will continue to operate until the
	 * time is up.
	 */
	 function debounce(func, wait, context) {
		var timer;
		return function debounced() {
			var context = $scope,
			args = Array.prototype.slice.call(arguments);
			$timeout.cancel(timer);
			timer = $timeout(function() {
				timer = undefined;
				func.apply(context, args);
			}, wait || 10);
		};
	 }
	/**
	 * Build handler to open/close a SideNav; when animation finishes
	 * report completion in console
	 */
	 function buildDelayedToggler(navID) {
		return debounce(function() {$mdSidenav(navID).toggle()}, 200);
	 }
})
.controller('FirstCtrl', ['$routeParams', function($routeParams) {

	this.name = "FirstCtrl";
	this.params = $routeParams;

}])
//.controller('CountersCtrl', function($routeParams, $scope, Izdelki, Test, $timeout) {
.controller('CountersCtrl', function($scope, $interval, Counter, Latency) {

	/* // Function to get the data
	$scope.getData = function(){
		Test.query().$promise.then(function(izdelek) {
			$scope.salesData = izdelek;
			console.log($scope.izdelek);
		});
	}
	
	// Function to replicate setInterval using $timeout service.
	$scope.intervalFunction = function(){
		$timeout(function() {
			$scope.getData();
			$scope.intervalFunction();
		}, 1000)
	};
	
	// Kick off the interval
	$scope.intervalFunction(); */
	/* $scope.salesData=[
		{hour: 1,sales: 54},
		{hour: 2,sales: 66},
		{hour: 3,sales: 77},
		{hour: 4,sales: 70},
		{hour: 5,sales: 60},
		{hour: 6,sales: 63},
		{hour: 7,sales: 55},
		{hour: 8,sales: 47},
		{hour: 9,sales: 55},
		{hour: 10,sales: 30}
	]; */
	var hour = 1;
	$scope.salesData=[{hour: hour,sales: 0}];
	$scope.countData=[{hour: hour,sales: 0}];

	$interval(function(){
		hour=hour+1;
		//var sales= Math.round(Math.random() * 100);
		Counter.get().$promise.then(function(count) {
			console.log(count);
			if(count.counts) {
				$scope.count = count.counts;
				if($scope.countData.length > 30) {
					$scope.countData.shift();
				}
				//console.log("Pushing latency!");
				//console.log(hour);
				console.log($scope.countData);
				$scope.countData.push({hour: hour, sales: count.counts});
			}
			
			//console.log(izdelek[izdelek.length-1]);
			//$scope.salesData.push({hour: hour, sales:izdelek[izdelek.length-1]});
		});
		Latency.get().$promise.then(function(latency) {
			console.log(latency);
			if(latency.fifteen) {
				if($scope.salesData.length > 30) {
					$scope.salesData.shift();
				}
				//console.log("Pushing latency!");
				//console.log(hour);
				console.log($scope.salesData);
				$scope.salesData.push({hour: hour, sales: latency.fifteen});
			}
		});
	}, 2000, 0);

})
.directive('linearChart', function($parse, $window){
   return{
	  restrict:'EA',
	  template:"<svg width='850' height='200'></svg>",
	   link: function(scope, elem, attrs){
		   var exp = $parse(attrs.chartData);

           var salesDataToPlot=exp(scope);
           var padding = 20;
           var pathClass="path";
           var xScale, yScale, xAxisGen, yAxisGen, lineFun;

           var d3 = $window.d3;
           var rawSvg=elem.find('svg');
           var svg = d3.select(rawSvg[0]);

           scope.$watchCollection(exp, function(newVal, oldVal){
               salesDataToPlot=newVal;
               redrawLineChart();
           });

           function setChartParameters(){

               xScale = d3.scale.linear()
                   .domain([salesDataToPlot[0].hour, salesDataToPlot[salesDataToPlot.length-1].hour])
                   .range([padding + 5, rawSvg.attr("width") - padding]);

               yScale = d3.scale.linear()
                   .domain([0, d3.max(salesDataToPlot, function (d) {
                       return d.sales;
                   })])
                   .range([rawSvg.attr("height") - padding, 0]);

               xAxisGen = d3.svg.axis()
                   .scale(xScale)
                   .orient("bottom")
                   .ticks(salesDataToPlot.length - 1);

               yAxisGen = d3.svg.axis()
                   .scale(yScale)
                   .orient("left")
                   .ticks(5);

               lineFun = d3.svg.line()
                   .x(function (d) {
                       return xScale(d.hour);
                   })
                   .y(function (d) {
                       return yScale(d.sales);
                   })
                   .interpolate("basis");
           }
		 
		 function drawLineChart() {

			   setChartParameters();

			   svg.append("svg:g")
				   .attr("class", "x axis")
				   .attr("transform", "translate(0,180)")
				   .call(xAxisGen);

			   svg.append("svg:g")
				   .attr("class", "y axis")
				   .attr("transform", "translate(25,0)")
				   .call(yAxisGen);

			   svg.append("svg:path")
				   .attr({
					   d: lineFun(salesDataToPlot),
					   "stroke": "blue",
					   "stroke-width": 2,
					   "fill": "none",
					   "class": pathClass
				   });
		   }

		   function redrawLineChart() {

			   setChartParameters();

			   svg.selectAll("g.y.axis").call(yAxisGen);

			   svg.selectAll("g.x.axis").call(xAxisGen);

			   svg.selectAll("."+pathClass)
				   .attr({
					   d: lineFun(salesDataToPlot)
				   });
		   }

		   drawLineChart();
	   }
   };
});
