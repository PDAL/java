/******************************************************************************
* Copyright (c) 2016, hobu Inc.  (info@hobu.co)
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following
* conditions are met:
*
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in
*       the documentation and/or other materials provided
*       with the distribution.
*     * Neither the name of Hobu, Inc. nor the names of its
*       contributors may be used to endorse or promote products derived
*       from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
* FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
* COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
* OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
* AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
* OF SUCH DAMAGE.
****************************************************************************/

#include "JavaPipeline.hpp"
#ifdef PDAL_HAVE_LIBXML2
#include <pdal/XMLSchema.hpp>
#endif

using pdal::LogPtr;
using pdal::MetadataNode;
using pdal::PointId;
using pdal::PointViewSet;
using pdal::PointTableRef;
using pdal::Stage;
using pdal::point_count_t;

using std::string;
using std::stringstream;
using std::vector;

namespace libpdaljava
{

void CountPointTable::reset()
{
    for (PointId idx = 0; idx < numPoints(); idx++)
        if (!skip(idx))
            m_count++;
    FixedPointTable::reset();
}

PipelineExecutor::PipelineExecutor(string const& json, int level)
{
    setLogLevel(level);

    LogPtr log(pdal::Log::makeLog("javapipeline", &m_logStream));
    log->setLevel(m_logLevel);
    m_manager.setLog(log);

    stringstream strm;
    strm << json;
    m_manager.readPipeline(strm);

}

bool PipelineExecutor::validate()
{
    m_manager.prepare();

    return true;
}

point_count_t PipelineExecutor::execute()
{

    point_count_t count = m_manager.execute();
    m_executed = true;
    return count;
}

point_count_t PipelineExecutor::executeStream(point_count_t streamLimit)
{
    CountPointTable table(streamLimit);
    m_manager.executeStream(table);
    m_executed = true;
    return table.count();
}

const PointViewSet& PipelineExecutor::views() const
{
    if (!m_executed)
        throw java_error("Pipeline has not been executed!");

    return m_manager.views();
}

string PipelineExecutor::getSrsWKT2() const
{
    string output("");
    PointTableRef pointTable = m_manager.pointTable();

    pdal::SpatialReference srs = pointTable.spatialReference();
    output = srs.getWKT();

    return output;
}

string PipelineExecutor::getPipeline() const
{
    stringstream strm;
    pdal::PipelineWriter::writePipeline(m_manager.getStage(), strm);
    return strm.str();
}


string PipelineExecutor::getMetadata() const
{
    if (!m_executed)
        throw java_error("Pipeline has not been executed!");

    stringstream strm;
    MetadataNode root = m_manager.getMetadata().clone("metadata");
    pdal::Utils::toJSON(root, strm);
    return strm.str();
}


string PipelineExecutor::getSchema() const
{
    if (!m_executed)
        throw java_error("Pipeline has not been executed!");

    stringstream strm;
    MetadataNode root = pointTable().layout()->toMetadata().clone("schema");
    pdal::Utils::toJSON(root, strm);
    return strm.str();
}

MetadataNode computePreview(Stage* stage)
{
    if (!stage)
        throw java_error("no valid stage in QuickInfo");

    stage->preview();

    pdal::QuickInfo qi = stage->preview();
    if (!qi.valid())
        throw java_error("No summary data available for stage '" + stage->getName()+"'" );

    stringstream strm;
    MetadataNode summary(stage->getName());
    summary.add("num_points", qi.m_pointCount);
    if (qi.m_srs.valid())
    {
        MetadataNode srs = qi.m_srs.toMetadata();
        summary.add(srs);
    }
    if (qi.m_bounds.valid())
    {
        MetadataNode bounds = pdal::Utils::toMetadata(qi.m_bounds);
        summary.add(bounds.clone("bounds"));
    }

    string dims;
    auto di = qi.m_dimNames.begin();

    while (di != qi.m_dimNames.end())
    {
        dims += *di;
        ++di;
        if (di != qi.m_dimNames.end())
           dims += ", ";
    }
    if (dims.size())
        summary.add("dimensions", dims);
    pdal::Utils::toJSON(summary, strm);
    return summary;
}


string PipelineExecutor::getQuickInfo() const
{

    Stage* stage(nullptr);
    vector<Stage *> stages = m_manager.stages();
    vector<Stage *> previewStages;

    for (auto const& s: stages)
    {
        auto n = s->getName();
        auto v = pdal::Utils::split2(n,'.');
        if (v.size() > 0)
            if (pdal::Utils::iequals(v[0], "readers"))
                previewStages.push_back(s);
    }

    MetadataNode summary;
    for (auto const& stage: previewStages)
    {
        MetadataNode n = computePreview(stage);
        summary.add(n);
    }

    stringstream strm;
    pdal::Utils::toJSON(summary, strm);
    return strm.str();
}

void PipelineExecutor::setLogStream(std::ostream& strm)
{

    LogPtr log(pdal::Log::makeLog("javapipeline", &strm));
    log->setLevel(m_logLevel);
    m_manager.setLog(log);
}


void PipelineExecutor::setLogLevel(int level)
{
    if (level < 0 || level > 8)
        throw java_error("log level must be between 0 and 8!");

    m_logLevel = static_cast<pdal::LogLevel>(level);
    setLogStream(m_logStream);
}


int PipelineExecutor::getLogLevel() const
{
    return static_cast<int>(m_logLevel);
}

} //namespace libpdaljava
