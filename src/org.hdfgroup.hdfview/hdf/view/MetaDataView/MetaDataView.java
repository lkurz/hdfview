/*****************************************************************************
 * Copyright by The HDF Group.                                               *
 * Copyright by the Board of Trustees of the University of Illinois.         *
 * All rights reserved.                                                      *
 *                                                                           *
 * This file is part of the HDF Java Products distribution.                  *
 * The full copyright notice, including terms governing use, modification,   *
 * and redistribution, is contained in the files COPYING and Copyright.html. *
 * COPYING can be found at the root of the source code distribution tree.    *
 * Or, see https://support.hdfgroup.org/products/licenses.html               *
 * If you do not have access to either file, you may request a copy from     *
 * help@hdfgroup.org.                                                        *
 ****************************************************************************/

package hdf.view.MetaDataView;

import hdf.object.Attribute;
import hdf.object.HObject;
import hdf.view.DataView.DataView;

/**
 *
 *The metadata view interface for displaying metadata information
 *
 * @author Peter X. Cao
 * @version 2.4 9/6/2007
 */
public abstract interface MetaDataView extends DataView {
    /** Add an attribute to a data object.
     *
     * @param obj  the attribute to add
     *
     * @return the Attribute object
     */
    public abstract Attribute addAttribute(HObject obj);

    /** Delete an attribute from a data object.
     *
     * @param obj  the attribute to delte
     *
     * @return the Attribute object
     */
    public abstract Attribute deleteAttribute(HObject obj);

}
