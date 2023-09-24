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

#pragma once

#include <pdal/PipelineManager.hpp>
#include <pdal/PipelineWriter.hpp>
#include <pdal/util/FileUtils.hpp>
#include <pdal/util/Utils.hpp>
#include <pdal/Stage.hpp>

#include <string>
#include <sstream>
#undef toupper
#undef tolower
#undef isspace

using pdal::point_count_t;

namespace libpdaljava
{

class java_error : public std::runtime_error
{
public:
    inline java_error(std::string const& msg) : std::runtime_error(msg)
        {}
};

class PDAL_DLL PipelineExecutor {
public:
    PipelineExecutor(std::string const& json, int level);
    virtual ~PipelineExecutor() = default;

    bool validate();
    point_count_t execute();
    point_count_t executeStream(point_count_t streamLimit);

    const pdal::PointViewSet& views() const;
    std::string getPipeline() const;
    std::string getMetadata() const;
    std::string getQuickInfo() const;
    std::string getSchema() const;
    std::string getSrsWKT2() const;
    pdal::PipelineManager const& getManager() const { return m_manager; }
    void setLogLevel(int level);
    int getLogLevel() const;
    std::string getLog() const { return m_logStream.str(); }

protected:
    virtual pdal::ConstPointTableRef pointTable() const { return m_manager.pointTable(); }

    pdal::PipelineManager m_manager;
    bool m_executed = false;

private:
    void setLogStream(std::ostream& strm);
    std::stringstream m_logStream;
    pdal::LogLevel m_logLevel;
};

class CountPointTable : public pdal::FixedPointTable
{
public:
    CountPointTable(point_count_t capacity) : pdal::FixedPointTable(capacity), m_count(0) {}
    point_count_t count() const { return m_count; }

protected:
    virtual void reset();

private:
    point_count_t m_count;
};

}
