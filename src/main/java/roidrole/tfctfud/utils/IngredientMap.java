package roidrole.tfctfud.utils;

import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class IngredientMap<V> implements Map<IIngredient<ItemStack>, V> {
	private final Map<Item, Node<V>> itemMap = new HashMap<>();
	private int size = 0;

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		if(!(key instanceof IIngredient)){
			return false;
		}
		List<?> validIngredients = ((IIngredient<?>) key).getValidIngredients();
		if(!(validIngredients.get(0) instanceof ItemStack)){
			return false;
		}
		for(ItemStack stack : (List<ItemStack>)validIngredients){
			Node<V> value = itemMap.get(stack.getItem());
			if(value == null){
				return false;
			} else {
				return value.containsKey(key);
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return itemMap.values().stream().anyMatch(entry -> entry.containsValue(value));
	}

	@Override
	public V get(Object key) {
		Optional<V> value = itemMap.values().stream()
			.map(Node::entrySet)
			.flatMap(Set::stream)
			.filter(entry -> entry.getKey() == key)
			.map(Entry::getValue)
			.findAny();
		return value.orElse(null);
	}

	public V get(ItemStack stack){
		Node<V> value = itemMap.get(stack.getItem());
		if(value == null){
			return null;
		}
		for (Entry<IIngredient<ItemStack>, V> entry : value.entrySet()){
			if(entry.getKey().test(stack)){
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public V put(IIngredient<ItemStack> key, V value) {
		List<ItemStack> matchingStacks = key.getValidIngredients();
		V old = null;
		for(ItemStack stack : matchingStacks){
			V oldEntry = itemMap.computeIfAbsent(stack.getItem(), (k) -> new Node<>()).put(key, value);
			if(oldEntry != null){
				old = oldEntry;
			}
		}
		if(old != null) {
			size++;
		}
		return old;
	}

	@Override
	public V remove(Object key) {
		if(!(key instanceof IIngredient)){
			return null;
		}
		List<?> validIngredients = ((IIngredient<?>) key).getValidIngredients();
		if(!(validIngredients.get(0) instanceof ItemStack)){
			return null;
		}
		V old = null;
		for(ItemStack stack : (List<ItemStack>)validIngredients){
			Node<V> value = itemMap.get(stack.getItem());
			if(value == null){
				continue;
			}
			V oldEntry = value.remove((IIngredient<ItemStack>) key);
			if(oldEntry != null){
				old = oldEntry;
			}
		}
		if(old != null){
			size--;
		}
		return old;
	}

	@Override
	public void putAll(@Nonnull Map<? extends IIngredient<ItemStack>, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	public void clear() {
		itemMap.clear();
		size = 0;
	}

	@Override
	public Set<IIngredient<ItemStack>> keySet() {
		return itemMap.values().stream().map(Node::getKeys).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override
	public Collection<V> values() {
		return itemMap.values().stream().map(Node::getValues).flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override
	public Set<Entry<IIngredient<ItemStack>, V>> entrySet() {
		return itemMap.values().stream().map(Node::entrySet).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	private static class Node<V> {
		List<IIngredient<ItemStack>> keys = new ArrayList<>(1);
		List<V> values = new ArrayList<>(1);

		public boolean containsKey(Object key){
			return keys.contains(key);
		}

		public boolean containsValue(Object value){
			return values.contains(value);
		}

		public List<IIngredient<ItemStack>> getKeys(){
			return keys;
		}

		public List<V> getValues(){
			return values;
		}

		public V remove(IIngredient<ItemStack> key){
			int index = keys.indexOf(key);
			keys.remove(index);
			return values.remove(index);
		}

		public V put(IIngredient<ItemStack> key, V value){
			int oldIndex = keys.indexOf(key);
			if(oldIndex == -1) {
				keys.add(key);
				values.add(value);
				return null;
			} else {
				return values.set(oldIndex, value);
			}
		}

		public Set<Entry<IIngredient<ItemStack>, V>> entrySet(){
			return new AbstractSet<Entry<IIngredient<ItemStack>, V>>() {
				@Override
				public Iterator<Entry<IIngredient<ItemStack>, V>> iterator() {
					return new Iterator<Entry<IIngredient<ItemStack>, V>>() {
						int index = -1;
						@Override
						public boolean hasNext() {
							return index + 1 < size();
						}

						@Override
						public Entry<IIngredient<ItemStack>, V> next() {
							index++;
							return new Entry<IIngredient<ItemStack>, V>() {
								@Override
								public IIngredient<ItemStack> getKey() {
									return keys.get(index);
								}

								@Override
								public V getValue() {
									return values.get(index);
								}

								@Override
								public V setValue(V value) {
									return values.set(index, value);
								}
							};
						}
					};
				}

				@Override
				public int size() {
					return keys.size();
				}
			};
		}
	}
}
