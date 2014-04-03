/* global define */

'use strict';

define(['angular', 'angular-resource'], function(angular) {

  /* Services */

  var services = angular.module('koansServices', ['ngResource']).value(
          'version', '0.1');

  services.factory('Suite', ['$resource', function($resource) {
    var Suite = $resource('suites/:name', {}, {})

    // enrich suite with custom methods

    /**
     * Add resolved koan id
     */
    Suite.prototype.addResolved = function(koanId) {
      if (!this.resolvedKoans) {
        this.resolvedKoans = []
      }
      if (this.resolvedKoans.indexOf(koanId) < 0) {
        this.resolvedKoans.push(koanId)
      }
    };
    
    /**
     * Save state of the suite
     */
    Suite.prototype.saveState = function() {
      console.log('saving state of suite:' + this.name)
      localStorage[this.name] = angular.toJson(this)
    };
    
    /**
     * Restore state of the suite
     */
    Suite.prototype.restoreState = function() {
      if (!this.resolvedKoans) {
        this.resolvedKoans = []
      }
      if (!localStorage[this.name]) { return; }
      console.log('restoring state of suite:' + this.name)
      try {
        var savedSuite = angular.fromJson(localStorage[this.name])
        if (savedSuite.resolvedKoans instanceof Array) {
          this.resolvedKoans = savedSuite.resolvedKoans
        } else {
          this.resolvedKoans = []
        }
      } catch (e) {
        console.error(e)
        this.resolvedKoans = []
      }
    }

    return Suite;
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