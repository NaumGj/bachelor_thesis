angular.module('IoTVisual.services', [])
.factory('Counter', function($resource) {
	return $resource('http://104.198.9.70:8080/counts', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('Latency', function($resource) {
	return $resource('http://104.198.9.70:8080/latency', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('TempEvent', function($resource) {
	return $resource('http://104.198.106.64:8080/fire/temperature', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('SmokeEvent', function($resource) {
	return $resource('http://104.198.106.64:8080/fire/smoke', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('Fire', function($resource) {
	return $resource('http://104.198.9.70:8080/fires', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('StolenProducts', function($resource) {
	return $resource('http://104.198.9.70:8080/stolenproducts', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('ShelfEvent', function($resource) {
	return $resource('http://104.198.106.64:8080/shoplifting/shelf', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('PaidEvent', function($resource) {
	return $resource('http://104.198.106.64:8080/shoplifting/paid', {}, {
		update: {
			method: 'PUT'
		}
	});
})
.factory('ExitEvent', function($resource) {
	return $resource('http://104.198.106.64:8080/shoplifting/exit', {}, {
		update: {
			method: 'PUT'
		}
	});
})
