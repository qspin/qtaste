# -*- coding: utf-8 -*-

import datetime

import github.GithubObject
import github.PaginatedList

import github.NamedUser
import github.Label


class Release(github.GithubObject.CompletableGithubObject):

    @property
    def created_at(self):
        """
        :type: datetime.datetime
        """
        self._completeIfNotSet(self._created_at)
        return self._created_at.value

    @property
    def body(self):
        """
        :type: string
        """
        self._completeIfNotSet(self._body)
        return self._body.value

    @property
    def tag_name(self):
        """
        :type: integer
        """
        self._completeIfNotSet(self._tag_name)
        return self._tag_name.value

    @property
    def name(self):
        """
        :type: string
        """
        self._completeIfNotSet(self._name)
        return self._name.value

    def _initAttributes(self):
        self._created_at = github.GithubObject.NotSet
        self._body = github.GithubObject.NotSet
        self._tag_name = github.GithubObject.NotSet
        self._name = github.GithubObject.NotSet

    def _useAttributes(self, attributes):
        if "created_at" in attributes:  # pragma no branch
            self._created_at = self._makeDatetimeAttribute(attributes["created_at"])
        if "body" in attributes:  # pragma no branch
            self._body = self._makeStringAttribute(attributes["body"])
        if "tag_name" in attributes:  # pragma no branch
            self._tag_name = self._makeStringAttribute(attributes["tag_name"])
        if "name" in attributes:  # pragma no branch
            self._name = self._makeStringAttribute(attributes["name"])
