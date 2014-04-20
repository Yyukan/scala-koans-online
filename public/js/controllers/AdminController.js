/* global define */

'use strict';

define(['angular', '../controllers'], function(angular, appControllers) {

  var AdminController = function($scope, Suite, Koan, AdminKoans) {
    // display suites
    $scope.suites = Suite.query()
    // display koans
    $scope.koans = Koan.query()
    // upload koans
    $scope.uploadKoans = function() {
      $scope.isUploading = true
      AdminKoans.upload(function() {
        $scope.isUploading = false
      })
    }
  };

  appControllers.controller('AdminController', ['$scope', 'Suite', 'Koan',
      'AdminKoans', AdminController]);
});