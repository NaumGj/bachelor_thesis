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
