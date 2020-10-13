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
package net.kyori.adventure.serializer.configurate4;

import java.lang.reflect.Type;
import java.util.List;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class BookTypeSerializer implements TypeSerializer<Book> {
  static final Class<Book> TYPE = Book.class;
  static final BookTypeSerializer INSTANCE = new BookTypeSerializer();
  static final String TITLE = "title";
  static final String AUTHOR = "author";
  static final String PAGES = "pages";

  private BookTypeSerializer() {
  }

  @Override
  public Book deserialize(final @NonNull Type type, final @NonNull ConfigurationNode value) throws ObjectMappingException {
    final Component title = value.node(TITLE).get(ComponentTypeSerializer.TYPE);
    final Component author = value.node(AUTHOR).get(ComponentTypeSerializer.TYPE);
    final List<Component> pages = value.node(PAGES).get(ComponentTypeSerializer.LIST_TYPE);
    if(title == null || author == null || pages == null) {
      throw new ObjectMappingException("title, author, and pages fields are all required to deserialize a Book");
    }
    return Book.book(title, author, pages);
  }

  @Override
  public void serialize(final @NonNull Type type, final @Nullable Book obj, final @NonNull ConfigurationNode value) throws ObjectMappingException {
    if(obj == null) {
      value.set(null);
      return;
    }
    value.node(TITLE).set(ComponentTypeSerializer.TYPE, obj.title());
    value.node(AUTHOR).set(ComponentTypeSerializer.TYPE, obj.author());
    value.node(PAGES).set(ComponentTypeSerializer.LIST_TYPE, obj.pages());
  }
}
