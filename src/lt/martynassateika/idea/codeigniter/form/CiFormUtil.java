/*
 * Copyright 2018 Martynas Sateika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lt.martynassateika.idea.codeigniter.form;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lt.martynassateika.idea.codeigniter.PhpExtensionUtil;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods related to the CI Forms.
 *
 * @author martynas.sateika
 * @since 0.4.0
 */
public class CiFormUtil {

  /**
   * Returns a list of form files whose relative path from a form directory equals the supplied
   * relative path.
   *
   * @param relativePath relative path to a form file
   * @param project current project
   * @return list of all matching form files
   */
  static List<PsiFile> findFormFiles(String relativePath, Project project) {
    PsiManager psiManager = PsiManager.getInstance(project);

    // If no extension is specified, it's a PHP file
    relativePath = PhpExtensionUtil.addIfMissing(relativePath);

    List<PsiFile> formFiles = new ArrayList<>();
    for (PsiFileSystemItem fileSystemItem : getFormDirectories(project)) {
      VirtualFile formDirectory = fileSystemItem.getVirtualFile();
      VirtualFile formFile = formDirectory.findFileByRelativePath(relativePath);
      if (formFile != null && !formFile.isDirectory()) {
        PsiFile psiFile = psiManager.findFile(formFile);
        if (psiFile != null) {
          formFiles.add(psiFile);
        }
      }
    }
    return formFiles;
  }

  /**
   * @param project current project
   * @return all directories called 'forms' in the project
   */
  static List<PsiFileSystemItem> getFormDirectories(Project project) {
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiFileSystemItem[] items = FilenameIndex.getFilesByName(project, "forms", scope, true);
    return Arrays.stream(items).filter(PsiFileSystemItem::isDirectory).collect(Collectors.toList());
  }

  /**
   * @param element an element
   * @param argIndex method parameter index (0-based)
   * @return {@code true} if {@code element} is an argument of a {@code load->form()} call
   */
  static boolean isArgumentOfLoadForm(PsiElement element, int argIndex) {
    return MyPsiUtil.isArgumentOfMethod(element, "load", "form", argIndex);
  }

}
