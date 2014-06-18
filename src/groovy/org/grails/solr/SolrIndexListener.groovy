/*
* Copyright 2010 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* ----------------------------------------------------------------------------
* Original Author: Mike Brevoort, http://mike.brevoort.com
* Project sponsored by:
*     Avalon Consulting LLC - http://avalonconsult.com
*     Patheos.com - http://patheos.com
* ----------------------------------------------------------------------------
*/

package org.grails.solr

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.hibernate.HibernateException
import org.hibernate.cfg.Configuration
import org.hibernate.event.*

class SolrIndexListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener, Initializable {

    private static final Logger log = Logger.getLogger(SolrIndexListener)

    public void onPostInsert(final PostInsertEvent event) {
        try {
            def entity = event.getEntity()
            if (ifEnabled(entity)) {
                entity.indexSolr()
                log.trace "Auto indexed ${entity} on insert"
            }
        }
        catch (HibernateException e) {
            log.error("Solr Index unable to process INSERT event", e)
        }
        return
    }

    public void onPostUpdate(final PostUpdateEvent event) {
        try {
            def entity = event.getEntity()
            if (ifEnabled(entity)) {
                entity.indexSolr()
                log.trace "Auto indexed ${entity} on update"
            }
        }
        catch (HibernateException e) {
            log.error("Solr Index unable to process UPDATE event", e)
        }
        return
    }

    public void onPostDelete(final PostDeleteEvent event) {
        try {
            def entity = event.getEntity()
            if (ifEnabled(entity)) {
                entity.deleteSolr()
            }
        } catch (HibernateException e) {
            log.error("Solr Index unable to process DELETE event", e)
        }
        return
    }

    private boolean ifEnabled(entity) {
        GrailsClassUtils.getStaticPropertyValue(entity.class, "enableSolrSearch") &&
            GrailsClassUtils.getStaticPropertyValue(entity.class, "solrAutoIndex")
    }

    public void initialize(final Configuration config) {
        return
    }
}