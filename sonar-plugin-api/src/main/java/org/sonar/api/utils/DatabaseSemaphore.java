/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.utils;

import org.sonar.api.BatchComponent;
import org.sonar.api.ServerComponent;

import java.util.Date;

/**
 * A semaphore shared among all the processes that can connect to the central database.
 *
 * @since 3.4
 */
public interface DatabaseSemaphore extends BatchComponent, ServerComponent {

  /**
   * Try to acquire a lock on a name, for a given duration.
   * The lock will be acquired if there's no existing lock on this name or if a lock exists but the max duration is reached.
   *
   * @param name the key of the semaphore
   * @param maxDurationInSeconds the max duration in seconds the semaphore will be acquired (a value of zero can be used to always acquire a lock)
   * @return a lock containing information if the lock could be acquired or not, the duration since locked, etc.
   */
  Lock acquire(String name, int maxDurationInSeconds);

  /**
   * Try to acquire the lock on a name.
   * The lock will be acquired only if there's no existing lock.
   *
   * @param name the key of the semaphore
   * @return a lock containing information if the lock could be acquired or not, the duration since locked, etc.
   */
  Lock acquire(String name);

  /**
   * Release the lock on a semaphore by its name.
   *
   * @param name the key of the semaphore
   */
  void release(String name);

  static class Lock {

    private String name;
    private boolean acquired;
    private Date locketAt;
    private Date createdAt;
    private Date updatedAt;
    private Long durationSinceLocked;

    public Lock(String name, boolean acquired, Date locketAt, Date createdAt, Date updatedAt) {
      this.name = name;
      this.acquired = acquired;
      this.locketAt = locketAt;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }

    public String getName() {
      return name;
    }

    public Date getLocketAt() {
      return locketAt;
    }

    public Date getCreatedAt() {
      return createdAt;
    }

    public Date getUpdatedAt() {
      return updatedAt;
    }

    public boolean isAcquired() {
      return acquired;
    }

    public Long getDurationSinceLocked() {
      return durationSinceLocked;
    }

    public void setDurationSinceLocked(Long durationSinceLocked) {
      this.durationSinceLocked = durationSinceLocked;
    }

  }

}