package me.yushust.inject.resolve;

import me.yushust.inject.resolve.solution.InjectableField;
import me.yushust.inject.resolve.solution.InjectableMethod;

import java.util.List;

/**
 * Represents an already resolved class
 */
class Solution {

	Object constructor = ConstructorResolver.CONSTRUCTOR_NOT_DEFINED;
	List<InjectableField> fields;
	List<InjectableMethod> methods;

}
