/* global define */

'use strict';

define(['angular', 'angular-resource'], function(angular) {

  /* Services */

  var services = angular.module('koansServices', ['ngResource']).value(
          'version', '0.1');

  services.factory('Suite', ['$resource', function($resource) {
    return $resource('suites/:name', {}, {})
  }])

  services.factory('Koan', ['$resource', function($resource) {
    return $resource('koans/:suite/:koan', {}, {})
  }])

  services.factory('AdminKoans', ['$resource', function($resource) {
    return $resource('koans/upload', {}, {
      upload: {}
    })
  }])

  services.factory('Compiler', ['$resource', function($resource) {
    return $resource('koans/compile', {}, {
      compile: {
        method: 'POST',
        params: {
          koan: '@content'
        }
      }
    })
  }])

});