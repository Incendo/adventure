/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.nbt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

final class CompoundBinaryTagImpl implements CompoundBinaryTag {
  static final CompoundBinaryTag EMPTY = new CompoundBinaryTagImpl(Collections.emptyMap());
  private final Map<String, BinaryTag> tags;
  private final int hashCode;

  CompoundBinaryTagImpl(final Map<String, BinaryTag> tags) {
    this.tags = Collections.unmodifiableMap(tags);
    this.hashCode = tags.hashCode();
  }

  public boolean contains(final @NonNull String key, final @NonNull BinaryTagType<?> type) {
    final /* @Nullable */ BinaryTag tag = this.tags.get(key);
    return tag != null && type.test(tag.type());
  }

  @Override
  public @NonNull Set<String> keySet() {
    return Collections.unmodifiableSet(this.tags.keySet());
  }

  @Override
  public @Nullable BinaryTag get(final String key) {
    return this.tags.get(key);
  }

  @Override
  public @NonNull CompoundBinaryTag put(final @NonNull String key, @NonNull final BinaryTag tag) {
    return this.edit(map -> map.put(key, tag));
  }

  @Override
  public byte getByte(final @NonNull String key, final byte defaultValue) {
    if(this.contains(key, BinaryTagTypes.BYTE)) {
      return ((NumberBinaryTag) this.tags.get(key)).byteValue();
    }
    return defaultValue;
  }

  @Override
  public short getShort(final @NonNull String key, final short defaultValue) {
    if(this.contains(key, BinaryTagTypes.SHORT)) {
      return ((NumberBinaryTag) this.tags.get(key)).shortValue();
    }
    return defaultValue;
  }

  @Override
  public int getInt(final @NonNull String key, final int defaultValue) {
    if(this.contains(key, BinaryTagTypes.INT)) {
      return ((NumberBinaryTag) this.tags.get(key)).intValue();
    }
    return defaultValue;
  }

  @Override
  public long getLong(final @NonNull String key, final long defaultValue) {
    if(this.contains(key, BinaryTagTypes.LONG)) {
      return ((NumberBinaryTag) this.tags.get(key)).longValue();
    }
    return defaultValue;
  }

  @Override
  public float getFloat(final @NonNull String key, final float defaultValue) {
    if(this.contains(key, BinaryTagTypes.FLOAT)) {
      return ((NumberBinaryTag) this.tags.get(key)).floatValue();
    }
    return defaultValue;
  }

  @Override
  public double getDouble(final @NonNull String key, final double defaultValue) {
    if(this.contains(key, BinaryTagTypes.DOUBLE)) {
      return ((NumberBinaryTag) this.tags.get(key)).doubleValue();
    }
    return defaultValue;
  }

  @Override
  public byte@NonNull[] getByteArray(final @NonNull String key) {
    if(this.contains(key, BinaryTagTypes.BYTE_ARRAY)) {
      return ((ByteArrayBinaryTag) this.tags.get(key)).value();
    }
    return new byte[0];
  }

  @Override
  public byte@NonNull[] getByteArray(final @NonNull String key, final byte@NonNull[] defaultValue) {
    if(this.contains(key, BinaryTagTypes.BYTE_ARRAY)) {
      return ((ByteArrayBinaryTag) this.tags.get(key)).value();
    }
    return defaultValue;
  }

  @Override
  public @NonNull String getString(final @NonNull String key, final @NonNull String defaultValue) {
    if(this.contains(key, BinaryTagTypes.STRING)) {
      return ((StringBinaryTag) this.tags.get(key)).value();
    }
    return defaultValue;
  }

  @Override
  public @NonNull ListBinaryTag getList(final @NonNull String key, final @NonNull ListBinaryTag defaultValue) {
    if(this.contains(key, BinaryTagTypes.LIST)) {
      return (ListBinaryTag) this.tags.get(key);
    }
    return defaultValue;
  }

  @Override
  public @NonNull ListBinaryTag getList(final @NonNull String key, final @NonNull BinaryTagType<? extends BinaryTag> expectedType, final @NonNull ListBinaryTag defaultValue) {
    if(this.contains(key, BinaryTagTypes.LIST)) {
      final ListBinaryTag tag = (ListBinaryTag) this.tags.get(key);
      if(expectedType.test(tag.listType())) {
        return tag;
      }
    }
    return defaultValue;
  }

  @Override
  public @NonNull CompoundBinaryTag getCompound(final @NonNull String key, final @NonNull CompoundBinaryTag defaultValue) {
    if(this.contains(key, BinaryTagTypes.COMPOUND)) {
      return (CompoundBinaryTag) this.tags.get(key);
    }
    return defaultValue;
  }

  @Override
  public int@NonNull[] getIntArray(final @NonNull String key) {
    if(this.contains(key, BinaryTagTypes.INT_ARRAY)) {
      return ((IntArrayBinaryTag) this.tags.get(key)).value();
    }
    return new int[0];
  }

  @Override
  public int@NonNull[] getIntArray(final @NonNull String key, final int@NonNull[] defaultValue) {
    if(this.contains(key, BinaryTagTypes.INT_ARRAY)) {
      return ((IntArrayBinaryTag) this.tags.get(key)).value();
    }
    return defaultValue;
  }

  @Override
  public long@NonNull[] getLongArray(final @NonNull String key) {
    if(this.contains(key, BinaryTagTypes.LONG_ARRAY)) {
      return ((LongArrayBinaryTag) this.tags.get(key)).value();
    }
    return new long[0];
  }

  @Override
  public long@NonNull[] getLongArray(final @NonNull String key, final long@NonNull[] defaultValue) {
    if(this.contains(key, BinaryTagTypes.LONG_ARRAY)) {
      return ((LongArrayBinaryTag) this.tags.get(key)).value();
    }
    return defaultValue;
  }

  private CompoundBinaryTag edit(final Consumer<Map<String, BinaryTag>> consumer) {
    final Map<String, BinaryTag> tags = new HashMap<>(this.tags);
    consumer.accept(tags);
    return new CompoundBinaryTagImpl(tags);
  }

  @Override
  public boolean equals(final Object that) {
    return this == that || (that instanceof CompoundBinaryTagImpl && this.tags.equals(((CompoundBinaryTagImpl) that).tags));
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  @Override
  public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("tags", this.tags));
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public @NonNull Iterator<Map.Entry<String, ? extends BinaryTag>> iterator() {
    return (Iterator) this.tags.entrySet().iterator();
  }

  @Override
  public void forEach(final @NonNull Consumer<? super Map.Entry<String, ? extends BinaryTag>> action) {
    this.tags.entrySet().forEach(requireNonNull(action, "action"));
  }
}
