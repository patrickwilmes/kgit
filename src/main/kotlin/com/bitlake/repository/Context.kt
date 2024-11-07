/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.repository

import java.nio.file.Paths

object Context {
    val wd = System.getProperty("user.dir")
    const val KGIT_DIR_NAME = ".kgit"
    val repository = Repository.getInstance(Paths.get(wd).toAbsolutePath())
}