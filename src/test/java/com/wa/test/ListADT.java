package com.wa.test;

public interface ListADT<ElementType> {

	// adds the specified element to the end of the list
	public void add(ElementType element);
	// return the element at the specified position in the list
	public ElementType get(int index);
	// returns the number of elements in the list
	public int size();
	// removes the element at the specified position in the list
	// all indices larger than the method argument 'index' become one smaller, thereby moving all subsequent elements to the left.
	public ElementType remove(int index);
	// removes all the elements from the list
	public void clear();
	
}
