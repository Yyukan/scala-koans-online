/* global define */

'use strict';

define(['angular', '../controllers'], function(angular, appControllers) {

  var NavbarController = function($scope, $rootScope, Suite, $filter) {

    function refreshStatistics() {
      var completedSuites = $scope.suites.reduce(function(x, suite) {
        if (suite.isComplete) { return x + 1; }
        return x;
      }, 0);
      var completedKoans = $scope.suites.reduce(function(x, suite) {
        return x + suite.resolvedKoans.length;
      }, 0);
      $scope.completedSuites = completedSuites;
      $scope.completedKoans = completedKoans;
    }

    $rootScope.$on('completeKoan', function(event, koan) {
      var suite = $scope.suite;
      suite.addResolved(koan.id);
      suite.saveState();
      $scope.suites.forEach(function(suite) {
        suite.restoreState();
      });
      refreshStatistics();
    });

    $rootScope.$on('prevSuite', function() {
      var suite = $scope.suite;
      if (!suite.isFirst) {
        $scope.selectSuite(suite.prev());
      }
    });

    $rootScope.$on('nextSuite', function() {
      var suite = $scope.suite;
      if (!suite.isLast) {
        $scope.selectSuite(suite.next());
      }
    });

    // display suites
    Suite.query(function(suites) {
      $scope.suites = $filter('orderBy')(suites, 'name');

      suites.forEach(function(suite) {
        suite.restoreState();
      });
      refreshStatistics();

      var suiteToSelect = $scope.suites[0]

      // restore last suite from storage
      var lastSuiteName = localStorage['lastSuite']
      if (lastSuiteName) {
        var found = $scope.suites.filter(function(suite) {
          return suite.name === lastSuiteName;
        })[0]
        if (found) {
          suiteToSelect = found
        }
      }

      $scope.selectSuite(suiteToSelect)
    })

    // select suite
    $scope.selectSuite = function(suite) {
      // clear selected state for all
      $scope.suites.forEach(function(suite) {
        suite.selected = false
      });

      // retrieve complete suite
      Suite.get(suite, function(suite) {
        // save to local storage
        localStorage["lastSuite"] = suite.name;

        suite.selected = true;
        suite.restoreState();

        if ($scope.suite) {
          $scope.suite.saveState();
        }
        
        var suites = $scope.suites;
        
        suite.isLast = suites[suites.length - 1].name === suite.name
        suite.isFirst = suites[0].name === suite.name
        
        suite.prev = function() {
          return suites[suites.indexOf(suites.filter(function(s) {
            return s.name === suite.name;
          })[0]) - 1];
        }
        suite.next = function() {
          return suites[suites.indexOf(suites.filter(function(s) {
            return s.name === suite.name;
          })[0]) + 1];
        }

        $scope.suite = suite;
        $rootScope.$emit('selectSuite', suite);
      });
    };

    // search suite
    $scope.search = function() {
      var suite = $scope.searchedSuite

      if (suite) {
        $scope.selectSuite(suite)
      }

      $scope.searchedSuite = ''
      $scope.focusSearch = false
    };

    // compile
    $scope.compile = function() {
      $rootScope.$emit('compileCurrentKoan');
    }
  };

  var deps = ['$scope', '$rootScope', 'Suite', '$filter', NavbarController];
  appControllers.controller('NavbarController', deps);
});