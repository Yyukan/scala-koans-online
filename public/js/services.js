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
     *
     * @return true if new koan is resolved
     *         false if koan has already been solved
     */
    Suite.prototype.addResolved = function(koanId) {
      this.resolvedKoans = this.resolvedKoans || [];

      if (this.resolvedKoans.indexOf(koanId) < 0) {
        this.resolvedKoans.push(koanId)
        return true;
      }
      return false;
    };

    Suite.prototype.isComplete = function() {
      return this.resolvedKoans.length == this.koans.length;
    }

    /**
     * Save state of the suite
     */
    Suite.prototype.saveState = function() {
      this.resolvedKoans = this.resolvedKoans || [];

      localStorage[this.name] = angular.toJson(this)
    };

    /**
     * Restore state of the suite
     */
    Suite.prototype.restoreState = function() {
      this.resolvedKoans = this.resolvedKoans || [];

      if (!localStorage[this.name]) { return; }
      try {
        var savedSuite = angular.fromJson(localStorage[this.name])

        this.resolvedKoans = savedSuite.resolvedKoans || [];
        this.selectedKoan = savedSuite.selectedKoan || 0;
      } catch (e) {
        console.error(e)
      }
    }

    return Suite;
  }])

  services.factory('Koan', ['$resource', function($resource) {
    var Koan = $resource('koans/:suite/:koan', {}, {});

    // enrich koan with custom methods

    Koan.prototype.saveState = function() {
      localStorage[this.suite + this.id] = angular.toJson(this)
    };

    Koan.prototype.restoreState = function() {
      var savedKoan = angular.fromJson(localStorage[this.suite + this.id])
      if (savedKoan) {
        this.content = savedKoan.content        
      }
    };

    return Koan;
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