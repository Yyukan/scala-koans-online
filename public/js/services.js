/* global define */

'use strict';

define(['angular', 'angular-resource'], function(angular) {

  /* Services */

  var services = angular.module('koansServices', ['ngResource']).value(
          'version', '0.1');

  services.factory('Suite', ['$resource', function($resource) {
    return $resource('suites/:name', {}, {
      query: {
        method: 'GET',
        params: {
          name: "@id"
        },
        isArray: true
      }
    })
  }])

  services.factory('Koan', ['$resource', function($resource) {
    return $resource('koans/:suite/:koan', {}, {
      query: {
        method: 'GET',
        params: {
          suite: '@id',
          koan: '@id'
        },
        isArray: false
      }
    })
  }])

  services.factory('Compiler', ['$resource', function($resource) {
    return $resource('koans/compile', {}, {
      compile: {
        method: 'POST',
        params: {
          koan: '@content'
        },
        isArray: false
      }
    })
  }])

});