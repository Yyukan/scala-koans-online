/* global define */

'use strict';

define(['angular', 'angular-resource'], function(angular) {

  /* Services */

  var services = angular.module('koansServices', ['ngResource']).value(
          'version', '0.1');

  services.factory('Suite', ['$resource', function($resource) {
    return $resource('suites/:suiteId', {}, {
      query: {
        method: 'GET',
        params: {
          suiteId: '@id'
        },
        isArray: true
      }
    })
  }])

  services.factory('Koan', ['$resource', function($resource) {
    return $resource('koans/:suiteId', {}, {
      query: {
        method: 'GET',
        params: {
          suiteId: '@id'
        },
        isArray: true
      }
    })
  }])

});