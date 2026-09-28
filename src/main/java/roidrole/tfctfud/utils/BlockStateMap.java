package roidrole.tfctfud.utils;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class BlockStateMap<V> implements Map<IBlockState, V> {
	private final Map<Block, Node<V>> blockMap = new HashMap<>();
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
		if(!(key instanceof IBlockState)){
			return false;
		}
		Block block = ((IBlockState) key).getBlock();
		
		return blockMap.containsKey(block);
	}

	@Override
	public boolean containsValue(Object value) {
		return blockMap.values().stream().anyMatch(entry -> entry.containsValue(value));
	}

	@Override
	public V get(Object key) {
		if(!(key instanceof IBlockState)){
			return null;
		}
		IBlockState blockState = (IBlockState) key;
		Node<V> node = blockMap.get((blockState).getBlock());
		if(node == null){
			return null;
		}
		return node.getValue(blockState);
	}

	@Override
	public V put(IBlockState key, V value) {
		Block block = key.getBlock();
		V old = blockMap.computeIfAbsent(block, (k) -> {
			Node<V> node;
			if(key.getProperties().isEmpty()){
				node = new NodeBlock(block, value);
			} else  {
				node = new NodeBlockState<>();
			}
			return node;
		}).put(key, value);
		if(old != null) {
			size++;
		}
		return old;
	}
	public void put(Block key, V value){
		this.blockMap.put(key, new NodeBlock(key, value));
	}

	@Override
	public V remove(Object key) {
		if(!(key instanceof IBlockState)){
			return null;
		}
		IBlockState blockState = (IBlockState) key;
		Node<V> value = blockMap.get((blockState).getBlock());
		if(value == null){
			return null;
		}
		V old = value.remove(blockState);
		if(old != null){
			size--;
		}
		return old;
	}

	public void remove(Block key) {
		Node<V> old = blockMap.remove(key);
		if(old == null){
			return;
		}
		size -= old.size();
	}

	@Override
	public void putAll(@Nonnull Map<? extends IBlockState, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	public void clear() {
		blockMap.clear();
		size = 0;
	}

	@Override @Nonnull
	public Set<IBlockState> keySet() {
		return blockMap.values().stream().map(Node::getKeys).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override @Nonnull
	public Collection<V> values() {
		return blockMap.values().stream().map(Node::getValues).flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override @Nonnull
	public Set<Entry<IBlockState, V>> entrySet() {
		return blockMap.values().stream().map(Node::entrySet).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	private interface Node<V> {
		boolean containsValue(Object value);

		List<IBlockState> getKeys();

		List<V> getValues();

		V getValue(IBlockState blockState);

		V remove(IBlockState key);

		V put(IBlockState key, V value);

		int size();

		Set<Entry<IBlockState, V>> entrySet();
	}

	private class NodeBlock implements Node<V> {
		private final V value;
		private final Block key;

		private NodeBlock(Block key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public boolean containsValue(Object value) {
			return value.equals(this.value);
		}

		@Override
		public List<IBlockState> getKeys() {
			return key.getBlockState().getValidStates();
		}

		@Override
		public List<V> getValues() {
			return Collections.singletonList(value);
		}

		@Override
		public V getValue(IBlockState blockState) {
			return value;
		}

		@Override
		public V remove(IBlockState key) {
			this.explode(key);
			return value;
		}

		@Override
		public V put(IBlockState key, V value) {
			if(value.equals(this.value)){
				return this.value;
			}
			if(key.getProperties().isEmpty()){
				blockMap.put(key.getBlock(), new NodeBlock(this.key, value));
				return this.value;
			}
			this.explode(key);
			return this.value;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Set<Entry<IBlockState, V>> entrySet() {
			return new AbstractSet<Entry<IBlockState, V>>() {
				final int size = key.getBlockState().getValidStates().size();
				@Override
				public Iterator<Entry<IBlockState, V>> iterator() {
					return new Iterator<Entry<IBlockState, V>>() {
						int index = 0;
						final ImmutableList<IBlockState> keys = key.getBlockState().getValidStates();
						@Override
						public boolean hasNext() {
							return index < size;
						}

						@Override
						public Entry<IBlockState, V> next() {
							return new AbstractMap.SimpleImmutableEntry<>(keys.get(index++), value);
						}
					};
				}

				@Override
				public int size() {
					return size;
				}
			};
		}

		//Creates a NodeBlockState containing all states but this one and puts it in the map.
		public void explode(IBlockState key) {
			blockMap.remove(this.key);
			if(key.getProperties().isEmpty()){
				return;
			}
			NodeBlockState<V> node = new NodeBlockState<>();
			for(IBlockState state: this.key.getBlockState().getValidStates()){
				if(state == key){
					continue;
				}
				node.put(state, this.value);
			}
			BlockStateMap.this.blockMap.put(this.key, node);
		}
	}

	private static class NodeBlockState<V> implements Node<V> {
		private final List<IBlockState> keys = new ArrayList<>(1);
		private final List<V> values = new ArrayList<>(1);

		public boolean containsValue(Object value){
			return values.contains(value);
		}

		public List<IBlockState> getKeys(){
			return keys;
		}

		public List<V> getValues(){
			return values;
		}

		public V getValue(IBlockState blockState){
			int index = keys.indexOf(blockState);
			if(index == -1){
				return null;
			}
			return values.get(index);
		}

		public V remove(IBlockState key){
			int index = keys.indexOf(key);
			if(index == -1){
				return null;
			}
			keys.remove(index);
			return values.remove(index);
		}

		public V put(IBlockState key, V value){
			int oldIndex = keys.indexOf(key);
			if(oldIndex == -1) {
				keys.add(key);
				values.add(value);
				return null;
			} else {
				return values.set(oldIndex, value);
			}
		}

		public int size(){
			return keys.size();
		}

		public Set<Entry<IBlockState, V>> entrySet(){
			return new AbstractSet<Entry<IBlockState, V>>() {
				@Override @Nonnull
				public Iterator<Entry<IBlockState, V>> iterator() {
					return new Iterator<Entry<IBlockState, V>>() {
						int index = -1;
						@Override
						public boolean hasNext() {
							return index + 1 < size();
						}

						@Override
						public Entry<IBlockState, V> next() {
							index++;
							return new Entry<IBlockState, V>() {
								@Override
								public IBlockState getKey() {
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
