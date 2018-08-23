package com.sonar.component;

import com.sonar.model.ProjectDO;
import com.sonar.model.ProjectUTDO;

public interface UTComponent {


    /**
     * kee cannot be blank
     *
     * @param projectDO
     * @return
     */
    ProjectUTDO queryUTList(ProjectDO projectDO);

    /**
     * kee cannot be blank
     *
     * @param projectDO
     * @return
     */
    ProjectUTDO queryUTFail(ProjectDO projectDO);



}
