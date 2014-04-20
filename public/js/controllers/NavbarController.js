/* global define */

'use strict';

define(['angular', '../controllers'], function(angular, appControllers) {

  var NavbarController = function($scope, $rootScope, Suite, $filter) {

    // initialize statistics storage
    if (localStorage['completedKoans'] == undefined) {
      localStorage['completedKoans'] = 0;
    }

    if (localStorage['completedSuites'] == undefined) {
      localStorage['completedSuites'] = JSON.stringify(new Array());
    }

    // display statistics
    $scope.completedKoans = localStorage['completedKoans'];
    var parsed = JSON.parse(localStorage['completedSuites']);
    $scope.completedSuites = parsed.length;

    // display suites
    Suite.query(function(suites) {
      $scope.suites = $filter('orderBy')(suites, 'name')

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