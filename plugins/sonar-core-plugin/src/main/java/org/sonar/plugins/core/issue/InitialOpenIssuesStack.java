/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.plugins.core.issue;

import edu.emory.mathcs.backport.java.util.Collections;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.batch.index.Cache;
import org.sonar.batch.index.Caches;
import org.sonar.core.issue.db.IssueChangeDto;
import org.sonar.core.issue.db.IssueDto;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class InitialOpenIssuesStack implements BatchExtension {

  private final Cache<String, IssueDto> issuesCache;
  private final Cache<String, ArrayList<IssueChangeDto>> issuesChangelogCache;

  public InitialOpenIssuesStack(Caches caches) {
    issuesCache = caches.createCache("last-open-issues");
    issuesChangelogCache = caches.createCache("issues-changelog");
  }

  public InitialOpenIssuesStack addIssue(IssueDto issueDto) {
    issuesCache.put(issueDto.getComponentKey(), issueDto.getKee(), issueDto);
    return this;
  }

  public List<IssueDto> selectAndRemoveIssues(String componentKey) {
    Iterable<IssueDto> issues = issuesCache.values(componentKey);
    List<IssueDto> result = newArrayList();
    for (IssueDto issue : issues) {
      result.add(issue);
    }
    issuesCache.clear(componentKey);
    return result;
  }

  public Iterable<IssueDto> selectAllIssues() {
    return issuesCache.allValues();
  }

  public InitialOpenIssuesStack addChangelog(IssueChangeDto issueChangeDto) {
    ArrayList<IssueChangeDto> changeDtos = issuesChangelogCache.get(issueChangeDto.getIssueKey());
    if (changeDtos == null) {
      changeDtos = newArrayList();
    }
    changeDtos.add(issueChangeDto);
    issuesChangelogCache.put(issueChangeDto.getIssueKey(), changeDtos);
    return this;
  }

  public List<IssueChangeDto> selectChangelog(String issueKey) {
    List<IssueChangeDto> changeDtos = issuesChangelogCache.get(issueKey);
    return changeDtos != null ? changeDtos : Collections.emptyList();
  }

  public void clear() {
    issuesCache.clearAll();
    issuesChangelogCache.clearAll();
  }
}
