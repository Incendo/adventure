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
package net.kyori.adventure.bossbar;

import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BossBarTest {
  private final AtomicInteger name = new AtomicInteger();
  private final AtomicInteger percent = new AtomicInteger();
  private final AtomicInteger color = new AtomicInteger();
  private final AtomicInteger overlay = new AtomicInteger();
  private final AtomicInteger flags = new AtomicInteger();
  private final BossBar.Listener listener = new BossBar.Listener() {
    @Override
    public void bossBarNameChanged(final @NonNull BossBar bar, final @NonNull Component oldName, final @NonNull Component newName) {
      BossBarTest.this.name.incrementAndGet();
    }

    @Override
    public void bossBarPercentChanged(final @NonNull BossBar bar, final float oldPercent, final float newPercent) {
      BossBarTest.this.percent.incrementAndGet();
    }

    @Override
    public void bossBarColorChanged(final @NonNull BossBar bar, final BossBar.@NonNull Color oldColor, final BossBar.@NonNull Color newColor) {
      BossBarTest.this.color.incrementAndGet();
    }

    @Override
    public void bossBarOverlayChanged(final @NonNull BossBar bar, final BossBar.@NonNull Overlay oldOverlay, final BossBar.@NonNull Overlay newOverlay) {
      BossBarTest.this.overlay.incrementAndGet();
    }

    @Override
    public void bossBarFlagsChanged(final @NonNull BossBar bar, final @NonNull Set<BossBar.Flag> oldFlags, final @NonNull Set<BossBar.Flag> newFlags) {
      BossBarTest.this.flags.incrementAndGet();
    }
  };
  private final BossBar bar = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

  @Test
  void testOfFlags() {
    final BossBar bar = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS, ImmutableSet.of(BossBar.Flag.DARKEN_SCREEN));
    assertThat(bar.flags()).containsExactly(BossBar.Flag.DARKEN_SCREEN);
  }

  // ====

  @Test
  void testName() {
    assertEquals(Component.text("A"), this.bar.name(Component.text("A")).name());
    assertEquals(0, this.name.get());

    this.bar.addListener(this.listener);
    assertEquals(Component.text("B"), this.bar.name(Component.text("B")).name());
    assertEquals(1, this.name.get());

    assertEquals(Component.text("B"), this.bar.name(Component.text("B")).name());
    assertEquals(1, this.name.get()); // value has not changed, should not have incremented
  }

  @Test
  void testPercent() {
    assertEquals(0f, this.bar.percent(0f).percent());
    assertEquals(0, this.percent.get());

    this.bar.addListener(this.listener);
    assertEquals(0.1f, this.bar.percent(0.1f).percent());
    assertEquals(1, this.percent.get());

    assertEquals(0.1f, this.bar.percent(0.1f).percent());
    assertEquals(1, this.percent.get()); // value has not changed, should not have incremented
  }

  @Test
  void testPercent_outOfRange() {
    assertThrows(IllegalArgumentException.class, () -> this.bar.percent(-1f));
    assertThrows(IllegalArgumentException.class, () -> this.bar.percent(1.1f));
  }

  @Test
  void testColor() {
    assertEquals(BossBar.Color.PINK, this.bar.color(BossBar.Color.PINK).color());
    assertEquals(0, this.color.get());

    this.bar.addListener(this.listener);
    assertEquals(BossBar.Color.PURPLE, this.bar.color(BossBar.Color.PURPLE).color());
    assertEquals(1, this.color.get());

    assertEquals(BossBar.Color.PURPLE, this.bar.color(BossBar.Color.PURPLE).color());
    assertEquals(1, this.color.get()); // value has not changed, should not have incremented
  }

  @Test
  void testOverlay() {
    assertEquals(BossBar.Overlay.NOTCHED_6, this.bar.overlay(BossBar.Overlay.NOTCHED_6).overlay());
    assertEquals(0, this.overlay.get());

    this.bar.addListener(this.listener);
    assertEquals(BossBar.Overlay.PROGRESS, this.bar.overlay(BossBar.Overlay.PROGRESS).overlay());
    assertEquals(1, this.overlay.get());

    assertEquals(BossBar.Overlay.PROGRESS, this.bar.overlay(BossBar.Overlay.PROGRESS).overlay());
    assertEquals(1, this.overlay.get()); // value has not changed, should not have incremented
  }

  @Test
  void testFlags() {
    assertEquals(ImmutableSet.of(), this.bar.flags(ImmutableSet.of()).flags());
    assertEquals(0, this.flags.get());

    final Changes changes = new Changes();

    this.bar.addListener(this.listener);
    this.bar.addListener(changes);

    // set flags to new from empty
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.DARKEN_SCREEN), this.bar.flags(ImmutableSet.of(BossBar.Flag.DARKEN_SCREEN)).flags());
      assertEquals(1, this.flags.get());
      assertThat(changes.flagsAdded.get()).containsExactly(BossBar.Flag.DARKEN_SCREEN);
      assertThat(changes.flagsRemoved.get()).isEmpty();
    });

    // set flags to same as existing
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.DARKEN_SCREEN), this.bar.flags(ImmutableSet.of(BossBar.Flag.DARKEN_SCREEN)).flags());
      assertEquals(1, this.flags.get()); // value has not changed, should not have incremented
      assertThat(changes.flagsAdded.get()).isEmpty();
      assertThat(changes.flagsRemoved.get()).isEmpty();
    });

    // set flags to new from existing
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC), this.bar.flags(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC)).flags());
      assertEquals(2, this.flags.get());
      assertThat(changes.flagsAdded.get()).containsExactly(BossBar.Flag.PLAY_BOSS_MUSIC);
      assertThat(changes.flagsRemoved.get()).containsExactly(BossBar.Flag.DARKEN_SCREEN);
    });

    // add flags
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.CREATE_WORLD_FOG), this.bar.addFlag(BossBar.Flag.CREATE_WORLD_FOG).flags());
      assertEquals(3, this.flags.get());
      assertThat(changes.flagsAdded.get()).containsExactly(BossBar.Flag.CREATE_WORLD_FOG);
      assertThat(changes.flagsRemoved.get()).isEmpty();
    });
    assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC), this.bar.flags(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC)).flags());
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.CREATE_WORLD_FOG), this.bar.addFlags(BossBar.Flag.CREATE_WORLD_FOG).flags());
      assertEquals(5, this.flags.get());
      assertThat(changes.flagsAdded.get()).containsExactly(BossBar.Flag.CREATE_WORLD_FOG);
      assertThat(changes.flagsRemoved.get()).isEmpty();
    });
    assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC), this.bar.flags(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC)).flags());
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.CREATE_WORLD_FOG), this.bar.addFlags(ImmutableSet.of(BossBar.Flag.CREATE_WORLD_FOG)).flags());
      assertEquals(7, this.flags.get());
      assertThat(changes.flagsAdded.get()).containsExactly(BossBar.Flag.CREATE_WORLD_FOG);
      assertThat(changes.flagsRemoved.get()).isEmpty();
    });

    // remove flags
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC), this.bar.removeFlag(BossBar.Flag.CREATE_WORLD_FOG).flags());
      assertEquals(8, this.flags.get());
      assertThat(changes.flagsAdded.get()).isEmpty();
      assertThat(changes.flagsRemoved.get()).containsExactly(BossBar.Flag.CREATE_WORLD_FOG);
    });
    assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.CREATE_WORLD_FOG), this.bar.addFlags(BossBar.Flag.CREATE_WORLD_FOG).flags());
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC), this.bar.removeFlags(BossBar.Flag.CREATE_WORLD_FOG).flags());
      assertEquals(10, this.flags.get());
      assertThat(changes.flagsAdded.get()).isEmpty();
      assertThat(changes.flagsRemoved.get()).containsExactly(BossBar.Flag.CREATE_WORLD_FOG);
    });
    assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.CREATE_WORLD_FOG), this.bar.addFlags(BossBar.Flag.CREATE_WORLD_FOG).flags());
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(BossBar.Flag.PLAY_BOSS_MUSIC), this.bar.removeFlags(ImmutableSet.of(BossBar.Flag.CREATE_WORLD_FOG)).flags());
      assertEquals(12, this.flags.get());
      assertThat(changes.flagsAdded.get()).isEmpty();
      assertThat(changes.flagsRemoved.get()).containsExactly(BossBar.Flag.CREATE_WORLD_FOG);
    });

    // clear flags
    changes.resetAndThen(() -> {
      assertEquals(ImmutableSet.of(), this.bar.flags(ImmutableSet.of()).flags());
      assertEquals(13, this.flags.get());
      assertThat(changes.flagsAdded.get()).isEmpty();
      assertThat(changes.flagsRemoved.get()).containsExactly(BossBar.Flag.PLAY_BOSS_MUSIC);
    });
  }

  static class Changes implements BossBar.Listener {
    final AtomicReference<Set<BossBar.Flag>> flagsAdded = new AtomicReference<>(Collections.emptySet());
    final AtomicReference<Set<BossBar.Flag>> flagsRemoved = new AtomicReference<>(Collections.emptySet());

    @Override
    public void bossBarFlagsChanged(final @NonNull BossBar bar, final @NonNull Set<BossBar.Flag> flagsAdded, final @NonNull Set<BossBar.Flag> flagsRemoved) {
      this.flagsAdded.set(flagsAdded);
      this.flagsRemoved.set(flagsRemoved);
    }

    public void resetAndThen(final Runnable runnable) {
      this.flagsAdded.set(Collections.emptySet());
      this.flagsRemoved.set(Collections.emptySet());
      runnable.run();
    }
  }
}
